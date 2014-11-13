local json = require("cjson")
local parser = require("redis.parser")
function comma_value(n)
        local left,num,right = string.match(n,'^([^%d]*%d)(%d*)(.-)$')
        return left..(num:reverse():gsub('(%d%d%d)','%1,'):reverse())..right
end
function getValue(a_key)
        local res = ngx.location.capture("/get_redis",{
                args = { key = a_key }
        })
        if res.status == 200 then
          return parser.parse_reply(res.body)
        else 
	  return "0.0"
        end
end
local group =json.decode(ngx.req.get_uri_args().group)
local pros = json.decode(ngx.req.get_uri_args(0).keys)
local tab = {}
for i=1,#pros do
  local key_args = pros[i]
  local true_key =group..":".. key_args 
  local key_value = getValue(true_key)
  if key_value~=nil then
      if tonumber(key_value)<0 then
	key_value ="0.00"
      end
  else
     key_value = "0.00"
  end
  tab[key_args]=comma_value(key_value)
end
ngx.say(json.encode(tab))

