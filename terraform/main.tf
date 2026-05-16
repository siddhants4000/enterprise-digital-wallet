terraform {
  required_version = ">= 1.6.0"

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.35"
    }

    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.17"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "kubernetes_namespace" "wallet" {
  metadata {
    name = "wallet"
  }
}

resource "helm_release" "wallet_platform" {
  name       = "wallet-platform"
  namespace  = kubernetes_namespace.wallet.metadata[0].name
  chart      = "../helm/wallet-platform"

  depends_on = [
    kubernetes_namespace.wallet
  ]
}