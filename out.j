.class public Kek
.super java/lang/Object


.method public static simpleFunc(I)I
	.limit stack 50
	.limit locals 50
	.var 0 is variable_0 I
	.var 1 is variable_1 I
	iload 0
	istore 1
	iload 1
	ldc 3
	iadd
	istore 1
	.var 2 is variable_2 I
	iload 1
	istore 2
	iload 2
	ireturn
.end method


.method public static entryPoint(II)V
	.limit stack 50
	.limit locals 50
	.var 0 is variable_0 I
	.var 1 is variable_1 I
	.var 2 is variable_2 I
	ldc 1
	istore 2
	return
.end method


.method public static main([Ljava/lang/String;)V
	.limit stack 50
	.limit locals 50
	invokestatic Kek/entryPoint()V
	return
.end method
