if redis.call('EXISTS', KEYS[1]) == 1 then
  return cjson.encode({status = 'COLLISION'})
end

local time = redis.call('TIME')
local now = tonumber(time[1]) * 1000 + math.floor(tonumber(time[2]) / 1000)
local idleTimeout = tonumber(ARGV[2])
local absoluteExpiresAt = now + tonumber(ARGV[3])
local expiresAt = math.min(now + idleTimeout, absoluteExpiresAt)
local session = {
  user_id = tonumber(ARGV[1]),
  created_at = now,
  absolute_expires_at = absoluteExpiresAt,
  roles = cjson.decode(ARGV[4])
}

redis.call('SET', KEYS[1], cjson.encode(session), 'PX', expiresAt - now)

return cjson.encode({
  status = 'CREATED',
  session = session,
  expires_at = expiresAt
})
