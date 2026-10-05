-- ARGV: session key pattern, member ID, JSON array of roles.
-- This full keyspace operation requires standalone Redis.
local userId = tonumber(ARGV[2])
if not userId or userId <= 0 then
  return redis.error_reply('Invalid member ID')
end

-- The application rejects empty role lists before calling this script.
local roles = cjson.decode(ARGV[3])

local time = redis.call('TIME')
local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000)
local updated = 0

for _, key in ipairs(redis.call('KEYS', ARGV[1])) do
  local value = redis.pcall('GET', key)
  if type(value) == 'string' then
    local decoded, session = pcall(cjson.decode, value)
    if decoded
        and type(session) == 'table'
        and session.user_id == userId
        and type(session.created_at) == 'number'
        and type(session.absolute_expires_at) == 'number'
        and session.absolute_expires_at > now
        and redis.call('PTTL', key) > 0 then
      session.roles = roles
      if redis.call('SET', key, cjson.encode(session), 'XX', 'KEEPTTL') then
        updated = updated + 1
      end
    end
  end
end

return updated
