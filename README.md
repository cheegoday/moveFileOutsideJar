# moveFileOutsideJar
when package a springboot project to a jar，copy all resources oustside the jar so that file system could find those resources<br>
springboot发布成jar包后，jar里面的文件系统封闭的，操作系统的文件系统无法找到jar里面的文件，因此需要全部移到jar外部。因此，写了这样一个自启动的类，一旦springboot项目启动，则自动复制jar内部的静态文件到jar外部的指定目录。
