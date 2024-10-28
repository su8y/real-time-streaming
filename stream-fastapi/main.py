import time

from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware
from starlette.responses import StreamingResponse

app = FastAPI()


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
@app.get("/")
async def root():
    return {"message": "Hello World"}


def read_file_line_by_line(filename):
    try:
        with open(filename, 'r', encoding='utf-8') as file:
            while True:
                line = file.readline()
                if not line:
                    break;
                yield f"data: {line.strip()}\n\n"
                time.sleep(200/1000.0)
    except FileNotFoundError:
        yield "data: Error: The File does not exist"
    except Exception as e:
        yield f"data: An error occured: {str(e)}\n\n"
@app.get("/stream/output")
async def output():
    filename = 'answer-stream.txt'
    return StreamingResponse(read_file_line_by_line(filename), media_type="text/event-stream")
