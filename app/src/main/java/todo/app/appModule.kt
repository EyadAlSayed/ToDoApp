package todo.app

import org.koin.core.module.Module
import org.koin.dsl.module
import todo.app.fragment.AddTaskFragment

val appModule : Module = module {
    single { AddTaskFragment() }
}