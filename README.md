# Marvel Heroes App

Sample project using MarvelApi that loads a list of characters and a screen with additional details.

## Introduction

This project is a conceptual Android app using Paging3 library with Room and a Network client to
load the data. I also used MVVM with ViewModels dispatching ViewStates, Koin as DI, Flow and
LiveData for background tasks.

## Observations

Paging3 is quite an intrusive library that makes it hard to implement with existing classes, in my
case, utility classes and interfaces that I like to use. The character detail screen is receiving the
full model by Arguments and then it calls the API another time to load it again, this second step
would not be necessary but I wanted to add the second API call to implement a non paginated and non
cached request.

## Author

- Javi Anyó

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/javieranyo/)
