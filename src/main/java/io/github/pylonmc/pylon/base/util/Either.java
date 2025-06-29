package io.github.pylonmc.pylon.base.util;

public sealed interface Either<L, R> {

    record Left<L, R>(L left) implements Either<L, R> {
    }

    record Right<L, R>(R right) implements Either<L, R> {
    }
}
