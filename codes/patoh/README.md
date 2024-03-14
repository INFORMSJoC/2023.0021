## Replicating

The PaToH-based heuristics can be re-run using the following commands.

To run PaToH-d:

```
cc -o benc_patoh_d benchmark_d.c   -L. -lpatoh -lm
./benc_patoh_d
```

To run PaToH-d:

```
cc -o benc_patoh_q benchmark_d.c   -L. -lpatoh -lm
./benc_patoh_q
```

They shouldn't take more than an hour.