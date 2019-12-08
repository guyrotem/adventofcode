function IntcodeMachine() {
    function runOpCode(code, instructionPointer, data) {
        let action = parseInstruction(code[instructionPointer]);

        if (action.opCode === 1) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3);
            code[paramsIndices[2]] = code[paramsIndices[0]] + code[paramsIndices[1]];
            return instructionPointer + 4;
        } else if (action.opCode === 2) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3);
            code[paramsIndices[2]] = code[paramsIndices[0]] * code[paramsIndices[1]];
            return instructionPointer + 4;
        } else if (action.opCode === 3) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 1);
            if (data.inputs.length <= 0)
                throw 'no inputs left at ptr ' + instructionPointer;
            code[paramsIndices[0]] = data.inputs.shift();
            return instructionPointer + 2;
        } else if (action.opCode === 4) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 1);
            data.output = code[paramsIndices[0]];
            return instructionPointer + 2;
        } else if (action.opCode === 5) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 2);
            if (code[paramsIndices[0]] !== 0)
                return code[paramsIndices[1]];
            else
                return instructionPointer + 3;
        } else if (action.opCode === 6) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 2);
            if (code[paramsIndices[0]] === 0)
                return code[paramsIndices[1]];
            else
                return instructionPointer + 3;
        } else if (action.opCode === 7) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3);
            if (code[paramsIndices[0]] < code[paramsIndices[1]])
                code[paramsIndices[2]] = 1;
            else
                code[paramsIndices[2]] = 0;
            return instructionPointer + 4;
        } else if (action.opCode === 8) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3);
            if (code[paramsIndices[0]] === code[paramsIndices[1]])
                code[paramsIndices[2]] = 1;
            else
                code[paramsIndices[2]] = 0;
            return instructionPointer + 4;
        } else if (action.opCode === 99) {
            return -1;
        } else {
            throw 'something is wrong, index: ' + instructionPointer + ' code: ' + code[instructionPointer];
        }
    }

    function calcParams(code, index, modes, numberOfParams) {
        return [...Array(numberOfParams).keys()].map(n => {
                if (modes[n] === 0)
                    return code[index + 1 + n];
                else
                    return index + 1 + n;
            }
        )
    }

    function parseInstruction(instruction) {
        let opCode = instruction % 100;
        let mode1 = Math.floor(instruction / 100) % 10;
        let mode2 = Math.floor(instruction / 1000) % 10;
        let mode3 = Math.floor(instruction / 10000) % 10;
        return {
            opCode: opCode,
            modes: [mode1, mode2, mode3]
        };
    }

    function runProgram(code, ...inputs) {
        let instructionPointer = 0;
        const data = {inputs: inputs};

        while (instructionPointer >= 0) {
            instructionPointer = runOpCode(code, instructionPointer, data);
        }
        return {code: code, output: data.output};
    }

    return {
        runProgram
    };
}

class StatefulProgram {

    constructor(code, phase) {
        this.code = code;
        this.instructionPointer = 0;
        this.inputs = [phase];
    }

    runUntilOutputOrHalt(inputs) {
        const data = {inputs: [...this.inputs, ...inputs], output: null};

        while (this.instructionPointer >= 0) {
            this.instructionPointer = runOpCode(this.code, this.instructionPointer, data);
            if (data.output !== null) {
                break;
            }
        }
        this.inputs = data.inputs;
        return {code: this.code, output: data.output};
    }
}