{{/* Define a template for the ClickHouse readiness check */}}
{{- define "observability-stack.clickhouse.readinessCheck" -}}
until wget -qO- http://clickhouse:8123/ping; do echo waiting for clickhouse; sleep 2; done;
{{- end -}}
