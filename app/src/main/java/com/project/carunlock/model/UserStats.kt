package com.project.carunlock.model

data class UserStats (
    var erroresTema : MutableList<Long>,
    var preguntasAcertadas : Long,
    var preguntasFalladas : Long,
    var preguntasTotales : Long,
    var testAprobados : Long,
    var testSuspendidos : Long,
    var testTotales : Long,
    var tiempoTotal : Long,
    var idsFailedQuestions : MutableList<String>)