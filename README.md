Yahoo Finance history fetcher using Akka HTTP

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.clicks
    val interval = "1d"
    val response = Await.result(fetcher.getStockHistory(ticker, startDate, endDate, interval), 100 seconds)
    println(response)
