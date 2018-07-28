package services

import play.api.inject.{SimpleModule, _}

class TasksModule extends SimpleModule(bind[ActorTask].toSelf.eagerly())


