# Ballista

Ballista ~is~ will be a proof-of-concept distributed compute platform based on Kubernetes and the Rust implementation of [Apache Arrow](https://arrow.apache.org/).

This is not my first attempt at building something like this. I originally wanted [DataFusion](https://github.com/apache/arrow/tree/master/rust/datafusion) to be a distributed compute platform but this was overly ambitious at the time, and it ended up becoming an in-memory query execution engine for the Rust implementation of Apache Arrow. However, DataFusion now provides a good foundation to have another attempt at building a [modern distributed compute platform](https://andygrove.io/how_to_build_a_modern_distributed_compute_platform/) in Rust.

My goal is to use this repo to move fast and try out ideas that eventually can be contributed back to Apache Arrow and to help drive requirements for Apache Arrow and DataFusion.

I will be working on this project in my spare time, which is limited, so progress will likely be slow. 

# PoC Status

- [X] README describing project
- [X] Skeleton protobuf file
- [X] Generate code from protobuf file
- [X] Implement skeleton gRPC server
- [X] Implement skeleton gRPC client
- [ ] Define real service and query plan in protobuf file
- [ ] CLI to create cluster using Kubernetes
- [ ] Example client to create query plan
- [ ] Client can send query plan
- [ ] Server can receive query plan
- [ ] Server can execute query plan using DataFusion
- [ ] Server can write results to CSV files
- [ ] Server can stream Arrow data back to client
- [ ] Benchmarks
- [ ] Implement Flight protocol

# Building

Currently depends on https://github.com/tower-rs/tower-grpc/tree/master/tower-grpc being cloned in a parallel directory.








 

