local bcpUtil= require("vis.visUtil")
local channel = 'sse:' .. ngx.req.get_uri_args(0).channel
local instance = bcpUtil.getRedisCon()
instance:select(1)
ngx.header["Content-Type"] = "text/event-stream"
--ngx.header["Cache-Control"] = "no-cache"
--ngx.header["Connection"] = "keep-alive"
ngx.header["Access-Control-Allow-Origin"] = "*"

local function exit_listen()
    local instanceNew = bcpUtil.getRedisCon()
    instanceNew:select(1)
    local message,err = instanceNew:rpush(channel,'exit') 
    if not message then
        ngx.say("failed to rpush reply: ", err)
    end
    bcpUtil.close(instanceNew);
end

local function my_cleanup()
     -- custom cleanup work goes here, like cancelling a pending DB transaction

     -- now abort all the "light threads" running in the current request handler
     exit_listen()
     ngx.log(ngx.CRIT, "clean up")
     -- ngx.exit(499)
 end

local ok, err = ngx.on_abort(my_cleanup)
if not ok then
     bcpUtil.close(instance);
     ngx.log(ngx.ERR, "failed to register the on_abort callback: ", err)
     ngx.exit(500)
end

local autoExit = 0
while true do
    autoExit = autoExit + 1 
    local message,err = instance:brpop(channel,50) 
    ngx.log(ngx.CRIT, "read message" .. autoExit);
    if not message then
        ngx.log(ngx.ERR, "failed to read reply: ", err)
        return
    end
    if message ~= ngx.null and message ~=nil then 
        autoExit = 0
        local info = message[2]
        if info == 'exit' then
            ngx.log(ngx.CRIT, "exit listen")
            bcpUtil.close(instance)
            return
        else 
            ngx.print("data:" .. info .. "\r\n\r\n")
            ngx.flush( true );
        end
    else
        if autoExit >= 30 then
            ngx.log(ngx.CRIT, "autoExit listen" .. autoExit)
            bcpUtil.close(instance)
            return
        end
    end
    
end
