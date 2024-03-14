## Replicating

Te KaHyPar-based heuristics can be re-run using the following commands.
```
cd kahypar/build
rm -rf *
cmake .. -DCMAKE_BUILD_TYPE=RELEASE
make KaHyPar
```

The are few dependencies needs to installed. Please checkout the original repo for more detailed instructions.
Original repo:https://github.com/kahypar/kahypar

After a successful build:

To run kKaHyPar:

```
python run_kahypar.py
```

To run kKaHyPar-E:

```
python run_kahypar_ga.py
```

To run kKaHyPar-EBQ:

```
python run_block.py
```

Each should take less than two hours.