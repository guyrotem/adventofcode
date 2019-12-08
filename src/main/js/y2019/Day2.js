function runWithInput(code, noun, verb) {
    const codeIJ = [...code];
    codeIJ[1] = noun; codeIJ[2] = verb;
    return IntcodeMachine().runProgram(codeIJ);
}

function solveDay2b(code, requestedOutput) {

    const intcodeMachine = IntcodeMachine();

    for (let noun = 0; noun < 100; noun++) {
        for (let verb = 0; verb < 100; verb++) {
            var codeIJ = [...code];
            codeIJ[1] = noun; codeIJ[2] = verb;
            intcodeMachine.runProgram(codeIJ);
            if (codeIJ[0] === requestedOutput) {
                return 100 * noun + verb;
            }
        }
    }

    return -1;
}