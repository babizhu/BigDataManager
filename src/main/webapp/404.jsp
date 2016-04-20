<%@ page language="java"
    contentType="application/json; charset=utf-8"
    pageEncoding="utf-8"%>

<%
response.addHeader( "Access-Control-Allow-Origin", "*" );
        response.addHeader( "Access-Control-Allow-Headers", "origin, content-type, accept" );

    String url = request.getScheme()+"://"+ request.getServerName()+request.getRequestURI()+"?"+request.getQueryString();



%>

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