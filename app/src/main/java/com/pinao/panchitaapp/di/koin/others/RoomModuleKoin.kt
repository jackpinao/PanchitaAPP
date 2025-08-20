package com.pinao.panchitaapp.di.koin.others

//import org.koin.core.module.dsl.factoryOf
//import org.koin.core.qualifier.named
//import org.koin.dsl.module

//val dataModule = module {
//    val DATABASE_NAME = "panchita_app"
//    single(named(Qualifier.ProvideDatabase)) {
//        Room.databaseBuilder(
//            get<Context>(),
//            AppDatabase::class.java,
//            DATABASE_NAME
//        ).build()
//    }
//    single(named(Qualifier.ProvideTicketDao)) {
//        get<AppDatabase>().ticketDao()
//    }
//    single(named(Qualifier.ProvideProductDao)) {
//        get<AppDatabase>().productDao()
//    }
//    single(named(Qualifier.ProvideDetailTicketDao)) {
//        get<AppDatabase>().detailTicketDao()
//    }
//    single(named(Qualifier.ProvideUserDao)) {
//        get<AppDatabase>().userDao()
//    }
//    single(named(Qualifier.ProvideClientDao)) {
//        get<AppDatabase>().clientDao()
//    }
//    single<UserRepository>(named(Qualifier.ProvideUserRepository)) {
//        UserRepositoryImpl(get())
//    }
//    single<RechangeRepository>(named(Qualifier.ProvideRechangeRepository)) {
//        RechangeRepositoryImpl(get())
//    }
//    factoryOf(::UserRepositoryImpl)
//    factoryOf(::RechangeRepositoryImpl)
//}