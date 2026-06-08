package com.example.cryptoinsighta.model.modelsFinanceAPI

data class YahooFinanceResponse(
    val chart: Chart
)

data class Chart(
    val result: List<ChartResult>?,
    val error: Any?
)

data class ChartResult(
    val timestamp: List<Long>?,
    val indicators: Indicators,
)


data class Indicators(
    val quote: List<Quote>
)

data class Quote(
    val close: List<Double?>    // prezzi di chiusura giornalieri
)