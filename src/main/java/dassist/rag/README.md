RAG documentation:

- modular design explanation 

1. Indexing - prompt the question
- transforming raw documents in the knowledge base into vector representations that can be efficiently searched later. This step corresponds to the write phase of the Retrieval-Augmented Generation (RAG) pipeline.
- workflow: 
- i. Load documents from the knowledge-base directory.
- ii. Extract the document text and metadata (e.g. source).
- iii. Generate embeddings using an OpenAI embedding model.
- iv. Store embeddings and metadata in an in-memory embedding store.

-output: A populated embedding store containing:

        Vector embeddings
        Original text segments
        Metadata (e.g. document source)

2. Retrieval - check answer within the knowledge-base
- retrieves the most relevant text segments from the embedding store based on a user query. This step represents the read phase of the RAG pipeline.
- workflow: 
- i. Receive a user query.
- ii. Convert the query into an embedding.
- iii. Perform similarity search in the embedding store.
- iv. Return the most relevant text segments.

- output: 
  - one or more relevant TextSegment objects
  - each segments includes:
    - retrieved text
    - associated metadata (source information)
    

3. Citation - annotate the source
- ensures transparency and explainability by linking generated answers back to their original sources. This is a critical component of trustworthy RAG systems.
- workflow: 
- i. extract metadata from retrieved text segments
- ii. generate an answer using an LLM with retrieved context
- iii. attach citation information to the generated answer

-output: 
    - a final answer genrated by the language model 
    - clear citation showing the source