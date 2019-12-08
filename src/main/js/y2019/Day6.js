function buildGraph(input) {
    let neighbors = {};

    function addNeighbor(from, to) {
        if (!neighbors[from]) {
            neighbors[from] = [];
        }

        neighbors[from].push(to);
    }

    input.forEach(x => {
        let edge = x.split(')');
        addNeighbor(edge[0], edge[1]);
    });

    return neighbors;
}

class Orbits {

    constructor(input, root) {
        this.root = root;
        this.neighbors = buildGraph(input);
        this.reverseNeighbors = reverse(this.neighbors);
        this.ranks = this.bfs(this.neighbors);
    }

    bfs() {
        let ranks = {};

        let bfsIteration = (rank, from) => {
            ranks[from] = rank;
            let next = this.neighbors[from] || [];
            next.map(v => bfsIteration(rank + 1, v)).reduce((acc, cur) => acc + cur, 0);
        };

        bfsIteration(0, this.root);
        return ranks;
    }

    lca(a, b) {
        var aAncestor = this.climbUp(a, Math.max(this.ranks[a] - this.ranks[b], 0));
        var bAncestor = this.climbUp(b, Math.max(this.ranks[b] - this.ranks[a], 0));

        while (aAncestor !== bAncestor) {
            aAncestor = this.climbUp(aAncestor, 1);
            bAncestor = this.climbUp(bAncestor, 1);
        }

        return aAncestor;
    }

    climbUp(from, levels) {
        if (levels === 0)
            return from;
        else
            return this.climbUp(this.reverseNeighbors[from][0], levels - 1);
    }
}

function reverse(neighbors) {
    let reverseNeighbors = {};

    function addNeighbor(from, to) {
        if (!reverseNeighbors[from]) {
            reverseNeighbors[from] = [];
        }

        reverseNeighbors[from].push(to);
    }

    for (let key in neighbors) {
        neighbors[key].forEach(v => {
            addNeighbor(v, key);
        });
    }

    return reverseNeighbors;
}

function sixA(input) {
    let orbits = new Orbits(input, 'COM');
    return Object.values(orbits.ranks).reduce((acc, cur) => acc + cur, 0);
}

function sixB(input) {
    let orbits = new Orbits(input, 'COM');
    let youSanLca = orbits.lca('YOU', 'SAN');

    return orbits.ranks['YOU'] + orbits.ranks['SAN'] - 2 * orbits.ranks[youSanLca] - 2;
}
