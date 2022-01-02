package io.github.amanshuraikwar.testutil

import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.KtorRemoteDataSource
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher

class FakeRemoteDataSource(
    engine: MockEngine,
    ioDispatcher: CoroutineDispatcher = FakeCoroutinesDispatcherProvider.io,
    remoteDataSource: RemoteDataSource = KtorRemoteDataSource.createInstance(
        engine = engine,
        ltaAccountKey = "fakeLtaAccountKey",
        addLoggingInterceptors = true,
        ioDispatcher = ioDispatcher
    )
) : RemoteDataSource by remoteDataSource {
    constructor(
        responsePredicate: (url: Url) -> String
    ) : this(
        engine = MockEngine { requestParams ->
            respond(
                content = ByteReadChannel(
                    responsePredicate(requestParams.url)
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    )
}