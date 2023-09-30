(START)
// check if any kbd input
      @KBD
      D=M

      @BLACK
      D;JGT
      @WHITE
      0;JMP

// Set values in R2 for white or black
(WHITE)
      @R2
      M=0
      @DRAW
      0;JMP
(BLACK)
      @R2
      M=-1
      @DRAW
      0;JMP

(DRAW)
// Init values for loop counter of 256 * 512 = 8192
      @8192
      D=A
      @R0
      M=D

// init pointer to screen and store it
      @SCREEN
      D=A
      @R1
      M=D

(LOOP)
//load colour
      @R2
      D=M
// load curr pointer pos
      @R1
      A=M
// draw
      M=D
// incr pointer and store
      D=A+1
      @R1
      M=D

// dec loop
      @R0
      M=M-1
      D=M

      @LOOP
      D;JGT

      @START
      0;JMP
