local added = redis.call('SADD', KEYS[1], ARGV[1])
if added == 0 then
  return 0
end

local ttl = redis.call('TTL', KEYS[1])
if ttl < 0 then
  redis.call('EXPIRE', KEYS[1], ARGV[3])
end

redis.call('HINCRBY', KEYS[2], ARGV[2], 1)
return 1
