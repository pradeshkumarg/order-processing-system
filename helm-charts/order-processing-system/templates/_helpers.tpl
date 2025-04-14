{{/* Define a template for the Kafka readiness check */}}
{{- define "order-processing-system.kafka.readinessCheck" -}}
until nc -z kafka 29092; do echo waiting for kafka; sleep 2; done;
{{- end -}}

{{/* Define a template for the Zookeeper readiness check */}}
{{- define "order-processing-system.zookeeper.readinessCheck" -}}
until nc -z zookeeper 2181; do echo waiting for zookeeper; sleep 2; done;
{{- end -}}

{{/* Define a template for the Postgres readiness check */}}
{{- define "order-processing-system.postgres.readinessCheck" -}}
until pg_isready -h postgres -p 5432; do echo waiting for postgres; sleep 2; done;
{{- end -}}
