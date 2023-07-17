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
    if event == "UNIT_SPELLCAST_SUCCEEDED" then -- 주문 시전 성공 이벤트인 경우
        local name, realm = UnitName(unitTarget) -- 유닛의 이름과 서버명을 가져옵니다.
        if IsInGroup() then -- 플레이어가 파티에 속해 있는지 확인합니다.
            if spellID == 453 then -- 주문 ID가 453인 경우 (원하는 주문 ID로 변경해야 함)
                local spellLink = select(1, GetSpellLink(spellID)) -- 주문 ID를 링크 형식으로 가져옵니다.
                SendChatMessage(name .. " Casting on " .. spellLink, "PARTY") -- 주문 시전 정보를 파티 채팅으로 전송합니다.
            end
        end
    end
end
```
> 죠낸또이또이 Casting on [정신의 위안]

# 2. API functions

## 2.1 [GetSpellLink(spellId)](https://wowpedia.fandom.com/wiki/API_GetSpellLink)

주문에 대한 하이퍼링크를 반환합니다.

**Example**
```lua
local spellId = 10060 -- 주문 ID로 사용할 값 (원하는 주문 ID로 변경해야 함)
local spellLink = select(1, GetSpellLink(spellId)) -- 주문 ID를 링크 형식으로 가져옵니다.
SendChatMessage("Casting on " .. spellLink, "PARTY") -- 주문 시전 정보를 파티 채팅으로 전송합니다.

```
> Casting on [마력 주입]

## 2.2 [SetRaidTarget(unit, index)](https://wowpedia.fandom.com/wiki/API_SetRaidTarget)


**Example 1**
```lua
SetRaidTarget("mouseover", 1) -- 별
SetRaidTarget("mouseover", 2) -- 동그라미
SetRaidTarget("mouseover", 3) -- 다이아
SetRaidTarget("mouseover", 4) -- 역삼
SetRaidTarget("mouseover", 5) -- 달
SetRaidTarget("mouseover", 6) -- 네모
SetRaidTarget("mouseover", 7) -- 엑스
SetRaidTarget("mouseover", 8) -- 해골
```
**Example 2**
```lua
local targetIndex = GetRaidTargetIndex("mouseover") -- 마우스 오버한 대상의 징표 번호를 가져옵니다.
if targetIndex ~= 8 then -- 현재 할당된 징표 번호가 8이 아닌 경우에만 실행합니다.
    SetRaidTarget("mouseover", 8) -- 해골 징표를 할당합니다.
end
```

# 3. 
```
```
# 4.
```
```
