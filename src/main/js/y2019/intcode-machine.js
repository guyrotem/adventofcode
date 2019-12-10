
function IntCodeMachine() {
    function runOpCode(code, instructionPointer, data) {
        let action = parseInstruction(code[instructionPointer]);

        if (action.opCode === 1) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3, data);
            write(code, paramsIndices[2], read(code, paramsIndices[0]) + read(code, paramsIndices[1]));
            return instructionPointer + 4;
        } else if (action.opCode === 2) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3, data);
            write(code, paramsIndices[2], read(code, paramsIndices[0]) * read(code, paramsIndices[1]));
            return instructionPointer + 4;
        } else if (action.opCode === 3) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 1, data);
            if (data.inputs.length <= 0) throw 'no inputs left at ptr ' + instructionPointer;
            write(code, paramsIndices[0], data.inputs.shift());
            return instructionPointer + 2;
        } else if (action.opCode === 4) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 1, data);
            data.output.push(read(code, paramsIndices[0]));
            return instructionPointer + 2;
        } else if (action.opCode === 5) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 2, data);
            if (read(code, paramsIndices[0]) !== 0)
                return read(code, paramsIndices[1]);
            else
                return instructionPointer + 3;
        } else if (action.opCode === 6) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 2, data);
            if (read(code, paramsIndices[0]) === 0)
                return read(code, paramsIndices[1]);
            else
                return instructionPointer + 3;
        } else if (action.opCode === 7) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3, data);
            if (read(code, paramsIndices[0]) < read(code, paramsIndices[1]))
                write(code, paramsIndices[2], 1);
            else
                write(code, paramsIndices[2], 0);
            return instructionPointer + 4;
        } else if (action.opCode === 8) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 3, data);
            if (read(code, paramsIndices[0]) === read(code, paramsIndices[1]))
                write(code, paramsIndices[2], 1);
            else
                write(code, paramsIndices[2], 0);
            return instructionPointer + 4;
        } else if (action.opCode === 9) {
            let paramsIndices = calcParams(code, instructionPointer, action.modes, 1, data);
            data.baseOffset = data.baseOffset + read(code, paramsIndices[0]);
            return instructionPointer + 2;
        } else if (action.opCode === 99) {
            return -1;
        } else {
            throw 'something is wrong, index: ' + instructionPointer + ' code: ' + code[instructionPointer];
        }
    }

    function read(code, index) {
        return code[index] || 0;
    }

    function write(code, index, value) {
        return code[index] = value;
    }

    function calcParams(code, index, modes, numberOfParams, data) {
        return [...Array(numberOfParams).keys()].map(n => {
                if (modes[n] === 0)
                    return read(code, index + 1 + n);
                else if (modes[n] === 1)
                    return index + 1 + n;
                else
                    return read(code, index + 1 + n) + data.baseOffset;
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
        const data = {inputs: inputs, baseOffset: 0, output: []};

        while (instructionPointer >= 0) {
            instructionPointer = runOpCode(code, instructionPointer, data);
        }
        return {code: code, output: data.output};
    }

    return {
        runProgram,
        runOpCode
    }
}

class StatefulProgram {

    constructor(code, phase) {
        this.code = code;
        this.instructionPointer = 0;
        this.inputs = [phase];
        this.runOpCode = IntCodeMachine().runOpCode
    }

    runUntilOutputOrHalt(inputs) {
        const data = {inputs: [...this.inputs, ...inputs], output: null};

        while (this.instructionPointer >= 0) {
            this.instructionPointer = this.runOpCode(this.code, this.instructionPointer, data);
            if (data.output !== null) {
                break;
            }
        }
        this.inputs = data.inputs;
        return {code: this.code, output: data.output};
    }
}