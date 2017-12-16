define i32 @min(i32* %a, i32* %b) {
    
entry:
    ; Allocate memory for Imp variables
    %c = alloca i32
    %0 = load i32, i32* %a
    %1 = load i32, i32* %b
    ; Logical operation <=
    %2 = icmp sle i32 %0, %1
    br i1 %2, label %if.n1.true, label %if.n1.false
    
if.n1.true:
    ; Assignation of variable c
    %3 = load i32, i32* %a
    store i32 %3, i32* %c
    br label %if.n1
    
if.n1.false:
    ; Assignation of variable c
    %4 = load i32, i32* %b
    store i32 %4, i32* %c
    br label %if.n1
    
if.n1:
    %5 = load i32, i32* %c
    ret i32 %5
    ret i32 0
}


define i32 @max(i32* %a, i32* %b) {    
entry:
    ; Allocate memory for Imp variables
    %c = alloca i32
    %0 = load i32, i32* %a
    %1 = load i32, i32* %b
    ; Logical operation >=
    %2 = icmp sge i32 %0, %1
    br i1 %2, label %if.n2.true, label %if.n2.false
    
if.n2.true:
    ; Assignation of variable c
    %3 = load i32, i32* %a
    store i32 %3, i32* %c
    br label %if.n2
    
if.n2.false:
    ; Assignation of variable c
    %4 = load i32, i32* %b
    store i32 %4, i32* %c
    br label %if.n2
    
if.n2:
    %5 = load i32, i32* %c
    ret i32 %5
    ret i32 0
}
