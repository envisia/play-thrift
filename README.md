# Thrift Generator based on [Scrooge](https://github.com/twitter/scrooge)

This Thrift generator is written in Scala since it's based on the Project Scrooge.

It's mean to be a replacement for Scrooge and the apache thrift code generator.
Actually it is aimed for Thrift usage while using the Thrift HTTP Mode on top of Play! 2.5.x.
It generates a easy to use AbstractController where you just need to define your services and attach them to your Router.

## Status

This project is in a really early stage and the internal API is likely to change soon.
Please be aware when using it.

## TODO

- [ ] Documentation
- [ ] Client API (Play-WS and a lightweight alternative, that don't use Finagle)
- [ ] A stable API
- [ ] Twirl templates instead of Mustache
- [ ] Tests