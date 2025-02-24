.data
newline: .asciiz "\n"

.text
.globl main

main:
    li $v0, 5
    syscall
    move $t0, $v0 

    li $t1, 1 
    li $t2, 1 
    li $t3, 2

    li $t4, 0 
loop:
    li $v0, 4
    la $a0, newline
    syscall

    li $v0, 1
    move $a0, $t1
    syscall

    li $v0, 4
    la $a0, newline
    syscall

    add $t4, $t4, 1
    add $t5, $t1, $t2 
    add $t6, $t3, $t5
    move $t1, $t2 
    move $t2, $t3
    move $t3, $t6  

    blt $t4, $t0, loop

    li $v0, 10
    syscall

