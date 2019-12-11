function runSequential(code, phases) {

    const machines = phases.map(phase => new StatefulProgram([...code], [phase]));

    let prevResult;
    let result = 0;
    do {
        prevResult = result;
        result = machines.reduce((acc, machine, index) => {
            return machine.runUntilOutputOrHalt([acc]).output;
        }, prevResult);

    } while (result !== null);

    return prevResult;
}

function allPermutations(inputArr) {
    const results = [];

    function permute(arr, memo) {
        let cur;
        memo = memo || [];
        for (let i = 0; i < arr.length; i++) {
            cur = arr.splice(i, 1);
            if (arr.length === 0) {
                results.push(memo.concat(cur));
            }
            permute(arr.slice(), memo.concat(cur));
            arr.splice(i, 0, cur[0]);
        }

        return results;
    }

    return permute(inputArr);
}

function maxSequentialA(code) {
    const possibleInputs = allPermutations([0, 1, 2, 3, 4]);
    const outputs = possibleInputs.map(inp => {
        return runSequential(code, inp);
    });
    return min(outputs);
}

function maxSequentialB(code) {
    const possibleInputs = allPermutations([5, 6, 7, 8, 9]);
    const outputs = possibleInputs.map(inp => {
        return runSequential(code, inp);
    });
    return min(outputs);
}

//	why JS, why??
function min(arr) {
	return arr.reduce(function (a, b) {
		return Math.max(a, b);
	}, -1);
}