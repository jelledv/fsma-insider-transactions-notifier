FROM public.ecr.aws/lambda/java:11

WORKDIR /var/task

# Copy function code and runtime dependencies from Maven layout
COPY target/classes .
COPY target/dependency/* ./lib/

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "be.jelledv.fsmanotifier.FsmaNotifier::handleRequest" ]