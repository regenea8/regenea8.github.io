목차
===
[1. API event](#1-API-event)

[2. API functions](#2-api-functions)



# 1. API event

## 1.1 [UNIT_SPELLCAST_SUCCEEDED](https://wowpedia.fandom.com/wiki/UNIT_SPELLCAST_SUCCEEDED): unitTarget, castGUID, spellID

주문이 성공적으로 시전되면 발동됩니다. 주문에 저항해도 이벤트가 수신됩니다.

**Example**
```lua
function(event, unitTarget, castGUID, spellID)
    if event == "UNIT_SPELLCAST_SUCCEEDED" then
        local name, realm = UnitName(unitTarget)
        if IsInGroup() then
            if spellID == 453 then
                local spellLink = select(1, GetSpellLink(spellID))
                SendChatMessage(name .. " Casting on " .. spellLink, "PARTY")
            end
        end
    end
end
```
> 죠낸또이또이 Casting on [정신의 위안]

# 2. API functions

## 2.1 [GetSpellLink](https://wowpedia.fandom.com/wiki/API_GetSpellLink): GetSpellLink(spellId), GetSpellLink(spellName)

주문에 대한 하이퍼링크를 반환합니다.

**Example**
```lua
local spellID   = 10060
local spellLink = select(1, GetSpellLink(spellID))
SendChatMessage("Casting on " .. spellLink, "PARTY")
```
> Casting on [마력 주입]

## 3. 
```
```
## 4.
```
```
