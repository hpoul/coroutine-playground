package design.codeux.coroutine.playground

import mu.KotlinLogging
import org.slf4j.bridge.SLF4JBridgeHandler

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    initLogging()

    logger.info { "Starting playground ..." }
}

fun initLogging() {
    // install JUL to slf4j bridge (java util logging)
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
}
