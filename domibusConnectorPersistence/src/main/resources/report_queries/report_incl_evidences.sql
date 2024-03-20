select 
  myear as year,
  mmonth as month,
  party,
  service, 
  received+rec_e as received, 
  sent+sent_e 
  as sent
from 
  (select
    serviceTable.SERVICE as service,
     count(m.DELIVERED_BACKEND) as received, 
     count(m.DELIVERED_GW) as sent, 
     sum(e.received) as rec_e, 
     sum(e.sent) as sent_e, 
     extract(YEAR FROM case when m.DELIVERED_BACKEND is null then m.DELIVERED_GW else DELIVERED_BACKEND end) as myear, 
     extract(MONTH from  case when m.DELIVERED_BACKEND is null then m.DELIVERED_GW else DELIVERED_BACKEND end) as mmonth,
     case when m.DELIVERED_BACKEND is null then toParty.PARTY_ID else fromParty.PARTY_ID end   as party
   from 
     DOMIBUS_CONNECTOR_MESSAGE m
       join DOMIBUS_CONNECTOR_MESSAGE_INFO i on i.MESSAGE_ID=m.ID
       join DOMIBUS_CONNECTOR_SERVICE serviceTable on i.FK_SERVICE = serviceTable.ID
       join DOMIBUS_CONNECTOR_PARTY toParty on i.FK_TO_PARTY_ID = toParty.ID
       join DOMIBUS_CONNECTOR_PARTY fromParty on i.FK_FROM_PARTY_ID = fromParty.ID
       join (select 
                case when y.direction_target is not null and y.direction_target='GATEWAY' 
				then count(x.DELIVERED_NAT) else 0 end as RECEIVED,
                case when y.direction_target is not null and y.direction_target='BACKEND' 
				then count(x.DELIVERED_GW) else 0 end as SENT,
               x.MESSAGE_ID 
             from 
               DOMIBUS_CONNECTOR_EVIDENCE x
               join DOMIBUS_CONNECTOR_MESSAGE y on y.id=x.message_id
             where 
               x.TYPE not in ('SUBMISSION_ACCEPTANCE', 'SUBMISSION_REJECTION', 'RELAY_REMMD_FAILURE')
             group by 
               x.MESSAGE_ID, y.direction_target) e 
       on e.MESSAGE_ID=m.ID 
   where
     m.DELIVERED_BACKEND between ? and ?
     or
     m.DELIVERED_GW between ? and ?
   group by
       serviceTable.SERVICE,
     (extract(YEAR FROM case when m.DELIVERED_BACKEND is null then m.DELIVERED_GW else DELIVERED_BACKEND end)),
     (extract(MONTH from  case when m.DELIVERED_BACKEND is null then m.DELIVERED_GW else DELIVERED_BACKEND end)),
     (case when m.DELIVERED_BACKEND is null then toParty.PARTY_ID else fromParty.PARTY_ID end)
   ) x
order by 
  myear,
  mmonth,
  party,
  service