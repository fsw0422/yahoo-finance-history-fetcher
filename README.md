Yahoo Finance history fetcher using Akka HTTP

    "com.github.fsw0422" % "yahoofinancehistoryfetcher_2.12" % "0.1.0"

usage

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.clicks
    val interval = "1d"
    val response = Await.result(fetcher.getStockHistory(ticker, startDate, endDate, interval), 100 seconds)
    println(response)
