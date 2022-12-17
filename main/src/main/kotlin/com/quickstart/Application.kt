package com.quickstart

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
    build().args(*args).packages("com.quickstart").start()
}
