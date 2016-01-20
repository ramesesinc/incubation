<%
import com.rameses.util.*;
def condpath = "templates/html/condition_constraint_"; 
def actpath = "templates/html/action_param_"; 
%>

<html>
    <head>
        <style>
            .block {
                padding-left: 10px;
            }
        </style>
    </head>
    <body>
        <h2>Conditions</h2>
        <%rule.conditions.eachWithIndex { cond,i->%>
            <div class="block">
                <b>${ (!cond.varname) ? '' : cond.varname + ': ' }</b>
                <u>${cond.fact.title}</u>

                <%if(editable==true && rule.state!='DEPLOYED') {%>
                    <a href="editCondition" objid="${cond.objid}">[Edit]</a>&nbsp;&nbsp;
                    <a href="removeCondition" objid="${cond.objid}">[Remove]</a>&nbsp;&nbsp;
                <%}%>
                
                <% if( cond.constraints ) {%>
                    &nbsp;&nbsp;&nbsp;
                    <%cond.constraints.eachWithIndex { cons, idx->
                        try {
                            out.print( "<br>&nbsp;&nbsp;&nbsp;");
                            out.print( '<b>'+((!cons.varname) ? '' : cons.varname + ': ')+'</b>' ); 
                            out.print( cons.field?.title );
                            if(cons.operator!=null) {
                                if(cons.operator.symbol.contains("null")) {
                                    String expr = cons.operator.caption;
                                    if(expr == "is null") 
                                        expr = "is not specified";
                                    else
                                        expr = "is specified";
                                    out.print( "&nbsp;"+expr);
                                }
                                else {
                                    out.print( "&nbsp;${cons.operator?.caption}&nbsp;");
                                    if(cons.usevar==1) { 
                                        out.print("<b>${ cons.var.name }</b>"); 
                                    }
                                    else {
                                        def handler = cons.field.handler;
                                        if(!handler) handler = cons.field.datatype;
                                        switch( handler ) {
                                            case "boolean":
                                                break;
                                            case "decimal":
                                                out.print(cons.decimalvalue);
                                                break;
                                            case "integer":
                                                out.print(cons.intvalue);
                                                break;
                                            case "lookup":
                                                out.print( cons.listvalue*.value.join(","));
                                                break;    
                                            
                                            case "lov":
                                                out.print( cons.listvalue.join(",") );
                                                 break;
                                            case "var":
                                                 out.print( cons.var.name );
                                                 break;
                                            case "date":
                                                 out.print( "'" + cons.datevalue + "'" );
                                                 break;
                                            default:
                                                out.print( "'" + cons.stringvalue + "'" );
                                        }
                                    }
                                }
                            }
                        }
                        catch(e) {
                            out.print( e.message );
                        }
                     }%>
                <% } %>
            </div>
        <%}%>

        <h2>Actions</h2>
        <%rule.actions?.eachWithIndex { action, i->%>
            <div class="block">
                <u><b>${action.actiondef.title}</b></u> with the ff. paramaters:

                <%if(editable==true && rule.state!='DEPLOYED') {%>
                    <a href="editAction" objid="${action.objid}">[Edit]</a>
                    <a href="removeAction" objid="${action.objid}">[Remove]</a>&nbsp;&nbsp;
                    <br>
                <%}%>
                
                <table>
                    <% action.params.eachWithIndex{param, j-> %>
                        <tr>
                            <td valign="top"><b>${param.actiondefparam.title}:</b></td>
                            <td valign="top">
                                <%
                                    try {
                                         def handler = param.actiondefparam.handler;
                                         if(!handler) handler = param.actiondefparam.datatype;
                                         switch(handler) {
                                            case "lookup": 
                                                String expr = param.obj?.value;
                                                if(!expr) {
                                                    expr = "Not specified";
                                                }
                                                out.print( expr );
                                                break;
                                            case "var":    
                                                String expr = param.var?.name;
                                                if(!expr) {
                                                    expr = "Not specified";
                                                }
                                                out.print( expr );
                                                break;
                                            case "lov":    
                                                out.print( param.lov );
                                                break;
                                            case "expression":
                                                String expr = param.expr;
                                                if(!expr) {
                                                    expr = "Not specified";
                                                }
                                                else if(param.exprtype == "expression") {
                                                    expr = expr.replace('\n','<br>').replace('\t', '&nbsp;'.multiply(5)).replace('\\s', '&nbsp;' );
                                                }
                                                out.print( expr );
                                                break;
                                            case "boolean":
                                                if(param.booleanvalue==1 || param.booleanvalue == true) {
                                                    out.print( "Yes" );
                                                }
                                                else {
                                                    out.print( "No" );
                                                }
                                                break;
                                            default: 
                                                out.print( param.stringvalue );
                                                break;
                                         }
                                     }
                                     catch(e) {
                                        out.print( e.message );
                                     }
                                 %>
                             </td>    
                         </tr>
                     <%}%>
                </table>
             </div>  
        <%}%>
    </body>
</html>

