.globl _meth_.
_meth_.:
	pushq %rbp
	movq %rsp, %rbp
	subq $0, %rsp
	pushq _var___(%rip)
	pushq %rdi
	popq %rax
	popq %rbx
	addq %rbx, %rax
	pushq %rax
	popq %rax
	leave
	ret
.globl _main
_main:
	pushq %rbp
	movq %rsp, %rbp
.data
.comm _var___, 8, 4
.text
	pushq $3
	popq _var___(%rip)
	pushq $1
	popq %rax
	cmpq $1, %rax
	je .if_true_label_0
	jmp .if_false_label_0
.if_true_label_0:
	pushq $4
	popq %rdi
	callq _meth_.
	pushq _var___(%rip)
	popq %rax
	popq %rbx
	addq %rbx, %rax
	pushq %rax
	popq _var___(%rip)
	jmp .if_end_label_0
.if_false_label_0:
.if_end_label_0:
	leave
	retq
