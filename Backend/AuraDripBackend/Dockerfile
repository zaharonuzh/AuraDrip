# Використовуємо офіційний образ .NET SDK для збірки
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app
EXPOSE 8080

# Копіюємо csproj і відновлюємо залежності
COPY *.csproj ./
RUN dotnet restore

# Копіюємо весь інший код і публікуємо
COPY . ./
RUN dotnet publish -c Release -o out

# Використовуємо легший образ для запуску
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=build /app/out .

# Вказуємо, який файл запускати (твоя dll)
ENTRYPOINT ["dotnet", "AuraDripBackend.dll"]