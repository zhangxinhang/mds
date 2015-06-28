
local json = require("cjson")
local parser = require("redis.parser")
--#local args =json.decode(ngx.req.get_uri_args(0).keyvals)
--ngx.say(json.decode(ngx.req.get_uri_args().keyvals))
local str =ngx.req.get_uri_args().keyvals
local test = json.decode(str);
for i=1,#test do
    ngx.say(test[i].name)
    ngx.say(test[i].pass)
end

