        .data

        .text
        .globl main

main:
        li $v0, 5
        syscall
        move $s0, $v0

        move $a0, $s0
        jal recursion

        move $a0, $v0
        li $v0, 1
        syscall

        li $v0, 10
        syscall

recursion:
        beqz $a0, base_case
        addi $sp, $sp, -4
        sw $ra, 0($sp)

        addi $a0, $a0, -1
        jal recursion
        move $t0, $v0

        addi $a0, $a0, 1
        li $t4, 3
        mul $t1, $a0, $t4
        add $t0, $t0, $t0
        sub $v0, $t1, $t0
        addi $v0, $v0, 7

        lw $ra, 0($sp)
        addi $sp, $sp, 4
        jr $ra

base_case:
        li $v0, 2
        jr $ra
