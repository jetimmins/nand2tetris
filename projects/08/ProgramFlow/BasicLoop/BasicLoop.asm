@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@LCL
A=M
M=D
(LOOP_START)
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@LCL
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
A=A-1
M=M+D
@SP
M=M-1
A=M
D=M
@LCL
A=M
M=D
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
A=A-1
M=M-D
@SP
M=M-1
A=M
D=M
@ARG
A=M
M=D
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@LOOP_START
D;JNE
@LCL
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
