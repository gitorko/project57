# Use a base PostgreSQL image
FROM postgres:14

# Install necessary build tools and dependencies
RUN apt-get update && \
    apt-get install -y \
    postgresql-server-dev-14 \
    gcc \
    make \
    git \
    && rm -rf /var/lib/apt/lists/*

# Clone and install pg_hint_plan 1.4.2
RUN git clone https://github.com/ossc-db/pg_hint_plan.git /pg_hint_plan && \
    cd /pg_hint_plan && \
    # Check out version 1.4.2
    git checkout tags/REL14_1_4_2 && \
    make && \
    make install

# Clean up
RUN rm -rf /pg_hint_plan
