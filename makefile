local-db:
	docker volume create postgres-db-data
	docker-compose build
