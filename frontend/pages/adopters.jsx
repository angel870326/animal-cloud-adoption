import React from "react"
import Layout from '@/components/Layout'
import { title, content } from "@/styles/jss/animal-cloud-adoption.js";
import AdopterList from "@/components/AdopterListPage/AdopterList";

export default function AdoptersPage() {
  return (
    <Layout>
      <h1 style={title}>認養人排行榜</h1>
      <div style={content}>
        <AdopterList />
      </div>
    </Layout>
  )
}