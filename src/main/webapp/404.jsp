<%@ page language="java"
    contentType="application/json; charset=utf-8"
    pageEncoding="utf-8"

    %>

<%
response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );
//        request.getContextPath()
//request.getServletPath()

    String url = request.getScheme() +"://"+ request.getServerName()+":" + request.getLocalPort() +request.getAttribute("javax.servlet.error.message").toString();
    String qs = request.getQueryString();
    if( qs != null ){
        url += "?"+qs;
    }
    String a = request.getAttribute("javax.servlet.error.message").toString();

%>
<%--<%=request.getAttribute("javax.servlet.error.message")%>--%>


<%--<%=exception%>--%>

<%--<%--%>
<%--exception.printStackTrace(response.getWriter());--%>
<%--%>--%>

<%--<%--%>
<%--//    Response lastResponse =(Response) request.getAttribute("lastResponse");--%>
<%--//    response.setHeader("Access-Control-Allow-Origin","*");--%>
<%--%>--%>
{
    "errId": "404",
    "args": "<%out.print( url );%>"
    <%--"messageText":"${lastResponse.message.text}"--%>
}

<%--${basePath}--%>
<%--<% response.getOutputStream().print(  basePath )%>--%>