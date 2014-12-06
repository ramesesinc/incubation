SELECT DISTINCT 
    xb.objid, xb.state, xb.owner_name AS ownername, 
    xb.owner_address_text AS owneraddress, xb.businessname, 
    xb.address_text AS address, xb.activeyear, xb.bin 
FROM ( 
    SELECT objid, MAX(activeyear) AS activeyear  
    FROM business b  
    WHERE b.owner_name LIKE $P{ownername} AND b.state NOT IN ('CANCELLED','RETIRED') 
    GROUP BY objid 

    UNION 

    SELECT objid, MAX(activeyear) AS activeyear 
    FROM business b 
    WHERE b.businessname LIKE $P{tradename} AND b.state NOT IN ('CANCELLED','RETIRED') 
    GROUP BY objid 

    UNION 

    SELECT objid, MAX(activeyear) AS activeyear  
    FROM business b 
    WHERE b.bin LIKE $P{bin} AND b.state NOT IN ('CANCELLED','RETIRED') 
    GROUP BY objid 
)bt 
    INNER JOIN business xb ON (bt.objid=xb.objid AND bt.activeyear=xb.activeyear)  
ORDER BY bin 