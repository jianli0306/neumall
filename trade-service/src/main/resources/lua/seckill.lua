local stockKey = KEYS[1]
local userKey = KEYS[2]
local userId = ARGV[1]

-- 检查库存
local stock = redis.call('get', stockKey)
if not stock or tonumber(stock) <= 0 then
    return -1
end

-- 检查用户是否已参与
if redis.call('sismember', userKey, userId) == 1 then
    return 0
end

-- 执行秒杀操作
redis.call('decr', stockKey)
redis.call('sadd', userKey, userId)
return 1