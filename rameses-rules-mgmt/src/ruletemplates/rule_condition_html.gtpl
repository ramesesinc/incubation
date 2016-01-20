<% conditions.each { o-> %>
    <div>
        ${(!o.varname)? '' : o.varname + ':'} 
        <a href="openCondition" objid="${o.objid}">${o.fact.name}</a>
    </div>
<%}%>

