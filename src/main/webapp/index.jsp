<html>
<head>
<title>Using GET Method to Read Form Data</title>
</head>
<body>
<center>
<h1>Stock Quote</h1>
<ul>
<li><p><b>Stock Quote </b>
   <%= request.getParameter("symbol") %>
</p></li>
<li><p>
   <%
   String symbol=request.getParameter("symbol");
   int delay=0;
   if (request.getParameter("delay")!= null && request.getParameter("delay").trim().length()>0)
         delay=Integer.parseInt(request.getParameter("delay"));
   com.amdocs.stock.Stock stock=com.amdocs.stock.QuotationProvider.getInstance().getQuote(symbol,delay);
   out.println(stock);
   %>
</p></li>
<li>
<b>SuccessCount</b>
<%=  com.amdocs.stock.QuotationProvider.getInstance().getSuccessCount() %><br/>
</li>

<li>
<b>FallbackCount</b>
<%=  com.amdocs.stock.QuotationProvider.getInstance().getFallbackCount() %><br/>
</li>
<li>
<b>Short Circuit Count</b>
<%=  com.amdocs.stock.QuotationProvider.getInstance().getShortCircuitedCount() %><br/>
</li>

</ul>
</body>
</html>