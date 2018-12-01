package design.codeux.coroutine.playground

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import org.slf4j.MDC
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.concurrent.ForkJoinPool

private val logger = KotlinLogging.logger {}

val myForkJoinPool = ForkJoinPool(2)


@Suppress("RedundantSuspendModifier")
suspend fun sleepingFunc(message: String = "") {
    // simulating blocking IO, e.g. jdbc queries.
    Thread.sleep(200)
    logger.info { "Slept. $message" }
}
suspend fun delayingFunc(message: String = "") {
    // simulating blocking IO, e.g. jdbc queries.
    delay(200)
    logger.info { "Delayed. $message" }
}

fun mdcContextAdd(pair: Pair<String, String>) = MDCContext(contextMap = MDC.getCopyOfContextMap() + pair)

@UseExperimental(ObsoleteCoroutinesApi::class)
fun main(args: Array<String>) {
    initLogging()

    playgroundForMdc()
    playgroundForProducer()
}

@ObsoleteCoroutinesApi
@UseExperimental(ExperimentalCoroutinesApi::class)
fun playgroundForProducer() {
    runBlocking(mdcContextAdd("mdcTest" to "started")
            + myForkJoinPool.asCoroutineDispatcher()) {


        produce {
            var count = 0
            while (true) {
                val x = count++
                withContext(Dispatchers.IO) {
                    sleepingFunc("producing $x")
                    send(x)
                }
            }
        }
            .take(10)
            .takeWhile { it < 5 }
            .map {
                logger.info { "Mapping $it" }
                1
            }
            .sumBy { it }
            .also { logger.info { "We received $it elements." } }

    }
}

fun playgroundForMdc() {
    logger.info { "Starting playground ..." }
    withLoggingContext("mdcTest" to "started") {
        logger.info { "Setting mdcTest." }

        runBlocking(Dispatchers.IO) {
            logger.info { "Just lost mdc :-(" }
        }

        runBlocking(myForkJoinPool.asCoroutineDispatcher()) {
            logger.info { "Also no mdc." }
        }

        runBlocking(MDCContext() + Dispatchers.IO) {
            logger.info { "Inside blocking coroutine." }
            val launched = launch {
                logger.info { "    Inside launch" }
                val funcs = listOf(
                    async { sleepingFunc() },
                    async(myForkJoinPool.asCoroutineDispatcher()) { sleepingFunc() })
                logger.info { "        started sleepers." }
                funcs.map { it.await() }
                logger.info { "    end of launch" }
            }
            logger.info { "After launch block." }
            launched.join()
            logger.info { "finished launch." }

            logger.info { "==============================" }

            logger.info { "before changing MDC." }

            withLoggingContext("mdcTest" to "changed") {
                logger.info { "changed MDC" }
                withContext(MDCContext()) {
                    logger.info { "changed MDC" }
                    async { sleepingFunc("with MDCContext") }.await()
                }
                withContext(MDCContext()) {
                    async { sleepingFunc("with MDCContext") }.await()
                }
                async { sleepingFunc("without MDCContext") }.await()
                logger.info { "called sleeping func." }
            }
            logger.info { "after changing MDC." }

        }
    }



    // somehow withLoggingContext looks broken..
    runBlocking(
        mdcContextAdd("mdcTest" to "started")
                + Dispatchers.IO) {


        logger.info { "==============================" }

        logger.info { "before changing MDC."}

        withContext(mdcContextAdd("mdcTest" to "changed")) {
            logger.info { "changed MDC" }
            withContext(MDCContext()) {
                logger.info { "changed MDC" }
                async { sleepingFunc("with MDCContext") }.await()
            }
            withContext(MDCContext()) {
                async { sleepingFunc("with MDCContext") }.await()
            }
            async { sleepingFunc("without MDCContext") }.await()
            logger.info { "called sleeping func." }
        }

        logger.info { "after changing MDC."}

    }

    logger.info { "Exiting." }
}

fun initLogging() {
    // install JUL to slf4j bridge (java util logging)
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
}
