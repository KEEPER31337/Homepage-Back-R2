local value = redis.call('GET', KEYS[1])
if not value then
  return cjson.encode({status = 'INVALID'})
end

local decoded, session = pcall(cjson.decode, value)
if not decoded
    or type(session) ~= 'table'
    or type(session.user_id) ~= 'number'
    or type(session.created_at) ~= 'number'
    or type(session.absolute_expires_at) ~= 'number'
    or type(session.roles) ~= 'table' then
  redis.call('DEL', KEYS[1])
  return cjson.encode({status = 'MALFORMED'})
end

for _, role in ipairs(session.roles) do
  if type(role) ~= 'string' then
    redis.call('DEL', KEYS[1])
    return cjson.encode({status = 'MALFORMED'})
  end
end

local time = redis.call('TIME')
local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000)
local absoluteExpiresAt = session.absolute_expires_at

if now >= absoluteExpiresAt then
  redis.call('DEL', KEYS[1])
  return cjson.encode({status = 'EXPIRED', redis_time = now})
end

local pttl = redis.call('PTTL', KEYS[1])
if pttl == -2 then
  return cjson.encode({status = 'INVALID', redis_time = now})
end
if pttl == -1 then
  redis.call('DEL', KEYS[1])
  return cjson.encode({status = 'TTL_MISSING', redis_time = now})
end
if pttl <= 0 then
  redis.call('DEL', KEYS[1])
  return cjson.encode({status = 'EXPIRED', redis_time = now})
end

local expiresAt = now + pttl
local touched = false

if expiresAt > absoluteExpiresAt then
  redis.call('PEXPIREAT', KEYS[1], absoluteExpiresAt)
  expiresAt = absoluteExpiresAt
  touched = true
elseif pttl <= tonumber(ARGV[2]) then
  local newExpiresAt = math.min(now + tonumber(ARGV[1]), absoluteExpiresAt)
  if newExpiresAt > expiresAt then
    redis.call('PEXPIREAT', KEYS[1], newExpiresAt)
    expiresAt = newExpiresAt
    touched = true
  end
end

return cjson.encode({
  status = 'VALID',
  session = session,
  touched = touched,
  redis_time = now,
  expires_at = expiresAt
})
