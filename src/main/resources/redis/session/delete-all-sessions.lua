-- ARGV: session key pattern, member ID.
-- Deletes every session for this member, not every member's sessions.
-- This full keyspace operation requires standalone Redis.
local userId = tonumber(ARGV[2])
if not userId or userId <= 0 then
  return redis.error_reply('Invalid member ID')
end

local deleted = 0
for _, key in ipairs(redis.call('KEYS', ARGV[1])) do
  local value = redis.pcall('GET', key)
  if type(value) == 'string' then
    local decoded, session = pcall(cjson.decode, value)
    if decoded and type(session) == 'table' and session.user_id == userId then
      deleted = deleted + redis.call('DEL', key)
    end
  end
end

return deleted
