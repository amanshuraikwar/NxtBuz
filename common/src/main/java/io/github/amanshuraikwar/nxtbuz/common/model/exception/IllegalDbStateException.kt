package io.github.amanshuraikwar.nxtbuz.common.model.exception

import java.lang.IllegalStateException

/**
 * Represents an exception when the data in local DB is not as expected.
 * For eg. missing bus stop row, missing bus route row etc.
 * The best thing to do when this exception is thrown is to schedule local DB update.
 * @author amanshuraikwar
 * @since 26 May 2021 11:49:52 AM
 */
class IllegalDbStateException(message: String) : IllegalStateException(message)